package net.vanillaEssence;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

// import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL11;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
// import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;

public class vanillaEssenceClient implements ClientModInitializer {

  private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

  @Override
  public void onInitializeClient() {

    // System.out.println("patatita2");

    // Runnable render = new Runnable(){
    //   @Override
    //   public void run() {
        
    //     // Camera camera = CLIENT.gameRenderer.getCamera();
        
    //     // double d0 = camera.getPos().x;
    //     // double d1 = camera.getPos().y - .005D;
    //     // VoxelShape upperOutlineShape = world.getBlockState(pos).getShape(world, pos, collisionContext);
    //     // if (!upperOutlineShape.isEmpty())
    //     //   d1 -= upperOutlineShape.max(Direction.Axis.Y);
    //     // double d2 = camera.getPos().z;
        
    //     // double d0 = 0;
    //     // double d1 = 0;
    //     // double d2 = 0;
    //     // int x = 22;
    //     // int y = 52;
    //     // int z = 22;
    //     // int color = 0xFF0000;
    //     // int red = (color >> 16) & 255;
    //     // int green = (color >> 8) & 255;
    //     // int blue = color & 255;
        
    //     // RenderSystem.enableDepthTest();
    //     // RenderSystem.disableTexture();
    //     // RenderSystem.enableBlend();
    //     // RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
    //     // GL11.glBegin(GL11.GL_LINES);
        
    //     // RenderSystem.color4f(red / 255f, green / 255f, blue / 255f, 1f);
    //     // GL11.glVertex3d(x + .01 - d0, y - d1, z + .01 - d2);
    //     // GL11.glVertex3d(x - .01 + 1 - d0, y - d1, z - .01 + 1 - d2);
    //     // GL11.glVertex3d(x - .01 + 1 - d0, y - d1, z + .01 - d2);
    //     // GL11.glVertex3d(x + .01 - d0, y - d1, z - .01 + 1 - d2);
        
    //     // GL11.glEnd();
    //     // RenderSystem.disableBlend();
    //     // RenderSystem.enableTexture();

    //     System.out.println("patatita");
    //     render1();

    //   }

    //   private void render1() {
    //     int x = 0;
    //     int y = 0;
    //     int width = 10;
    //     int height = 10;
    //     int color = 0xFF0000;
    //     float zLevel = 55;

    //     // float a = 255;
    //     // float r = 255;
    //     // float g = 192;
    //     // float b = 203;
    //     float a = (float) (color >> 24 & 255) / 255.0F;
    //     float r = (float) (color >> 16 & 255) / 255.0F;
    //     float g = (float) (color >>  8 & 255) / 255.0F;
    //     float b = (float) (color & 255) / 255.0F;

    //     Tessellator tessellator = Tessellator.getInstance();
    //     BufferBuilder buffer = tessellator.getBuffer();

    //     RenderSystem.disableTexture();
    //     RenderSystem.enableBlend();
    //     RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
    //     RenderSystem.color4f(r, g, b, a);

    //     buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION);

    //     buffer.vertex(x        , y         , zLevel).next();
    //     buffer.vertex(x        , y + height, zLevel).next();
    //     buffer.vertex(x + width, y + height, zLevel).next();
    //     buffer.vertex(x + width, y         , zLevel).next();

    //     tessellator.draw();

    //     RenderSystem.enableTexture();
    //     RenderSystem.disableBlend();
    //   }
    // };
    
    // CLIENT.submit(render);
  }
}
