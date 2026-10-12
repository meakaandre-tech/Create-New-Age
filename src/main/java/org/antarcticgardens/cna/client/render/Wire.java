package org.antarcticgardens.cna.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.antarcticgardens.cna.config.CNAConfig;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * A sagging wire between two points. {@link #build} samples the world light along the wire and turns it into
 * quads in the space of the block the wire starts in; {@link #emit} writes those quads at render time.
 */
public class Wire {
    public static final float SAG_FACTOR = 0.9f;
    private static final Vector3f GLOBAL_UP = new Vector3f(0.0f, 1.0f, 0.0f);

    /** x, y, z, u, v per vertex, four vertices per quad. */
    private final float[] vertices;
    private final int[] lights;
    private final int count;

    private Wire(float[] vertices, int[] lights, int count) {
        this.vertices = vertices;
        this.lights = lights;
        this.count = count;
    }

    public static Wire build(Vector3f start, Vector3f end, float sectionsPerMeter, float thickness, int maxLength, Level level) {
        Vector3f difference = new Vector3f(end).sub(start);
        float totalLength = difference.length();
        if (totalLength < 1.0e-4f)
            return new Wire(new float[0], new int[0], 0);
        Vector3f direction = difference.div(totalLength);
        Vector3f up = calculateUp(direction);

        Matrix4f pose = new Matrix4f()
                .translate((float) (start.x - Math.floor(start.x)), (float) (start.y - Math.floor(start.y)), (float) (start.z - Math.floor(start.z)))
                .rotateTowards(direction, up);

        int maxSections = Mth.ceil((maxLength * Mth.sqrt(3.0F)) * sectionsPerMeter * 2.0);
        int sectionsAmount = Math.min((int) Math.ceil(totalLength * sectionsPerMeter), maxSections);
        float catenaryScalar = new Vector3f(up).mul(GLOBAL_UP).length();
        float lastCatenary = 0.0f;
        float sectionLength = 1.0F / sectionsPerMeter;

        int vertexCount = sectionsAmount * 8;
        float[] vertices = new float[vertexCount * 5];
        int[] lights = new int[vertexCount];
        int n = 0;

        for (int sectionId = 1; sectionId <= sectionsAmount; sectionId++) {
            float sectionOffset = sectionLength * sectionId;
            Vector3f lightPos = new Vector3f(start)
                    .add(new Vector3f(direction).mul(sectionOffset))
                    .add(new Vector3f(up).mul(lastCatenary));
            BlockPos lightBlockPos = BlockPos.containing(new Vec3(lightPos));
            int light = LightCoordsUtil.pack(level.getBrightness(LightLayer.BLOCK, lightBlockPos), level.getBrightness(LightLayer.SKY, lightBlockPos));

            float catenary = catenary(sectionId, totalLength, sectionsAmount) * catenaryScalar;
            float ht = thickness / 2;
            float vOffset = (sectionId % 2 == 0) ? 0.5f : 0.0f;
            float z0 = sectionLength * (sectionId - 1);
            for (int i = -1; i <= 1; i += 2) {
                n = put(vertices, lights, n, pose, ht, ht * i + lastCatenary, z0, 0.0f, 0.5f + vOffset, light);
                n = put(vertices, lights, n, pose, -ht, -ht * i + lastCatenary, z0, 0.0f, 0.0f + vOffset, light);
                n = put(vertices, lights, n, pose, -ht, -ht * i + catenary, z0 + sectionLength, 1.0f, 0.0f + vOffset, light);
                n = put(vertices, lights, n, pose, ht, ht * i + catenary, z0 + sectionLength, 1.0f, 0.5f + vOffset, light);
            }

            lastCatenary = catenary;
        }

        return new Wire(vertices, lights, n);
    }

    private static int put(float[] vertices, int[] lights, int n, Matrix4f pose, float x, float y, float z, float u, float v, int light) {
        Vector3f p = pose.transformPosition(x, y, z, new Vector3f());
        int o = n * 5;
        vertices[o] = p.x;
        vertices[o + 1] = p.y;
        vertices[o + 2] = p.z;
        vertices[o + 3] = u;
        vertices[o + 4] = v;
        lights[n] = light;
        return n + 1;
    }

    private static Vector3f calculateUp(Vector3f direction) {
        Vector3f right = new Vector3f(direction).cross(GLOBAL_UP);
        if (right.equals(new Vector3f(0.0f), 0.01f))
            return new Vector3f(1.0f, 0.0f, 0.0f);
        return right.cross(direction).normalize();
    }

    private static float catenary(double x, double length, int sections) {
        double a = length / CNAConfig.getServer().maxWireLength.get() * SAG_FACTOR;
        x = (x / sections * 2 - 1);
        return (float) ((Math.cosh(x) - Math.cosh(1.0f)) * a);
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public void emit(PoseStack.Pose pose, VertexConsumer consumer) {
        for (int i = 0; i < count; i++) {
            int o = i * 5;
            consumer.addVertex(pose, vertices[o], vertices[o + 1], vertices[o + 2])
                    .setColor(1.0f, 1.0f, 1.0f, 1.0f)
                    .setUv(vertices[o + 3], vertices[o + 4])
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(lights[i])
                    .setNormal(pose, 0.0f, 1.0f, 0.0f);
        }
    }
}
