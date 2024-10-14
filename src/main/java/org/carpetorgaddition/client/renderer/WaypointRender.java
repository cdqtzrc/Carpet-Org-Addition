package org.carpetorgaddition.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.carpetorgaddition.util.WorldUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public enum WaypointRender {
    /**
     * 常规
     */
    CONVENTION(Identifier.ofVanilla("textures/map/decorations/red_x.png")),
    /**
     * 导航器
     */
    NAVIGATOR(Identifier.ofVanilla("textures/map/decorations/target_x.png")),
    TAIGA_VILLAGE(Identifier.ofVanilla("textures/map/decorations/taiga_village.png"));
    /**
     * 路径点图标，来自原版地图
     */
    private final Identifier ICON;
    @Nullable
    private Vec3d target;
    @Nullable
    private String worldId;

    WaypointRender(Identifier identifier) {
        this.ICON = identifier;
    }

    public static void register() {
        // 注册路径点渲染器
        for (WaypointRender render : WaypointRender.values()) {
            WorldRenderEvents.LAST.register(render::drawWaypoint);
        }
    }

    /**
     * 绘制路径点
     */
    public void drawWaypoint(WorldRenderContext context) {
        MatrixStack matrixStack = context.matrixStack();
        MinecraftClient client = MinecraftClient.getInstance();
        Camera camera = client.gameRenderer.getCamera();
        if (camera == null) {
            return;
        }
        Vec3d vec3d = this.getPos(client);
        if (vec3d == null) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        drawIcon(context, matrixStack, vec3d, camera);
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        BackgroundRenderer.clearFog();
    }

    @Nullable
    private Vec3d getPos(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null || this.target == null || this.worldId == null) {
            return null;
        }
        String playerWorldId = WorldUtils.getDimensionId(player.getWorld());
        if (playerWorldId.equals(this.worldId)) {
            return this.target;
        }
        if (playerWorldId.equals(WorldUtils.OVERWORLD) && this.worldId.equals(WorldUtils.THE_NETHER)) {
            return new Vec3d(this.target.getX() * 8, this.target.getY(), this.target.getZ() * 8);
        }
        if (playerWorldId.equals(WorldUtils.THE_NETHER) && this.worldId.equals(WorldUtils.OVERWORLD)) {
            return new Vec3d(this.target.getX() / 8, this.target.getY(), this.target.getZ() / 8);
        }
        return null;
    }

    /**
     * 绘制路径点图标
     */
    private void drawIcon(WorldRenderContext context, MatrixStack matrixStack, Vec3d target, Camera camera) {
        // 获取摄像机位置
        Vec3d cameraPos = camera.getPos();
        // 玩家距离目标的位置
        Vec3d offset = target.subtract(cameraPos);
        // 获取客户端渲染距离
        int renderDistance = MinecraftClient.getInstance().options.getViewDistance().getValue() * 16;
        // 修正路径点渲染位置
        Vec3d correctionVec3d = new Vec3d(offset.getX(), offset.getY(), offset.getZ());
        if (correctionVec3d.length() > renderDistance) {
            // 将路径点位置限制在渲染距离内
            correctionVec3d = correctionVec3d.normalize().multiply(renderDistance);
        }
        matrixStack.push();
        // 将路径点平移到方块位置
        matrixStack.translate(correctionVec3d.getX(), correctionVec3d.getY(), correctionVec3d.getZ());
        // 固定路径点大小，防止因距离的改变而改变（远小近大）
        float scale = (float) correctionVec3d.length() / 30F;
        matrixStack.scale(scale, scale, scale);
        // 翻转路径点
        matrixStack.multiply(new Quaternionf(-1, 0, 0, 0));
        // 让路径点始终对准玩家
        matrixStack.multiply(new Quaternionf().rotateY((float) ((Math.PI / 180.0) * (camera.getYaw() - 180F))));
        matrixStack.multiply(new Quaternionf().rotateX((float) ((Math.PI / 180.0) * (-camera.getPitch()))));
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        // 渲染图标纹理
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f, -1F, -1F, 0F).texture(0, 0).overlay(OverlayTexture.DEFAULT_UV).normal(entry, 0F, 1F, 0F);
        bufferBuilder.vertex(matrix4f, -1F, 1F, 0F).texture(0, 1).overlay(OverlayTexture.DEFAULT_UV).normal(entry, 0F, 1F, 0F);
        bufferBuilder.vertex(matrix4f, 1F, 1F, 0F).texture(1, 1).overlay(OverlayTexture.DEFAULT_UV).normal(entry, 0F, 1F, 0F);
        bufferBuilder.vertex(matrix4f, 1F, -1F, 0F).texture(1, 0).overlay(OverlayTexture.DEFAULT_UV).normal(entry, 0F, 1F, 0F);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, ICON);
        // 将缓冲区绘制到屏幕上。
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        tessellator.clear();
        // 如果准星正在指向路径点，显示文本
        if (pointerPointing(camera, target)) {
            drawDistance(context, matrixStack, offset, tessellator);
        }
        matrixStack.pop();
    }

    /**
     * 绘制距离文本
     */
    private void drawDistance(WorldRenderContext context, MatrixStack matrixStack, Vec3d offset, Tessellator tessellator) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        // 计算距离
        double distance = offset.length();
        String formatted = distance >= 1000 ? "%.1fkm".formatted(distance / 1000) : "%.1fm".formatted(distance);
        MutableText text = Text.literal(formatted);
        // 获取文本宽度
        int width = textRenderer.getWidth(formatted);
        // 获取背景不透明度
        float backgroundOpacity = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
        int opacity = (int) (backgroundOpacity * 255.0F) << 24;
        matrixStack.push();
        // 缩小文字
        matrixStack.scale(0.15F, 0.15F, 0.15F);
        // 渲染文字
        textRenderer.draw(text, -width / 2F, 8, Colors.WHITE, false,
                matrixStack.peek().getPositionMatrix(), context.consumers(),
                TextRenderer.TextLayerType.SEE_THROUGH, opacity, 1);
        tessellator.clear();
        matrixStack.pop();
    }

    /**
     * @return 光标是否指向路径点
     * @see EndermanEntity#isPlayerStaring(PlayerEntity)
     */
    @SuppressWarnings("JavadocReference")
    private boolean pointerPointing(Camera camera, Vec3d target) {
        float f = camera.getPitch() * (float) (Math.PI / 180.0);
        float g = -camera.getYaw() * (float) (Math.PI / 180.0);
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        Vec3d vec3d = new Vec3d(i * j, -k, h * j).normalize();
        Vec3d vec3d2 = new Vec3d(target.getX() - camera.getPos().getX(), target.getY() - camera.getPos().getY(), target.getZ() - camera.getPos().getZ());
        double d = vec3d2.length();
        vec3d2 = vec3d2.normalize();
        double e = vec3d.dotProduct(vec3d2);
        return e > 0.999 - (0.025 / d);
    }

    public Vec3d getPos() {
        return target;
    }

    public void setTarget(Vec3d target, String world) {
        this.target = target;
        this.worldId = world;
    }

    public void clear() {
        this.target = null;
        this.worldId = null;
    }
}
