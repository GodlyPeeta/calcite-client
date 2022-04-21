package calcite.client.mixin.extension

import net.minecraft.client.renderer.RenderGlobal
import net.minecraft.client.renderer.ViewFrustum
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.shader.Framebuffer
import net.minecraft.client.shader.Shader
import net.minecraft.client.shader.ShaderGroup
import net.minecraft.util.math.BlockPos
import calcite.client.mixin.client.accessor.render.AccessorRenderGlobal
import calcite.client.mixin.client.accessor.render.AccessorRenderManager
import calcite.client.mixin.client.accessor.render.AccessorShaderGroup
import calcite.client.mixin.client.accessor.render.AccessorViewFrustum

val RenderGlobal.entityOutlineShader: ShaderGroup get() = (this as calcite.client.mixin.client.accessor.render.AccessorRenderGlobal).entityOutlineShader

val RenderManager.renderPosX: Double get() = (this as calcite.client.mixin.client.accessor.render.AccessorRenderManager).renderPosX
val RenderManager.renderPosY: Double get() = (this as calcite.client.mixin.client.accessor.render.AccessorRenderManager).renderPosY
val RenderManager.renderPosZ: Double get() = (this as calcite.client.mixin.client.accessor.render.AccessorRenderManager).renderPosZ
val RenderManager.renderOutlines: Boolean get() = (this as calcite.client.mixin.client.accessor.render.AccessorRenderManager).renderOutlines

val ShaderGroup.listShaders: List<Shader> get() = (this as calcite.client.mixin.client.accessor.render.AccessorShaderGroup).listShaders
val ShaderGroup.listFrameBuffers: List<Framebuffer> get() = (this as calcite.client.mixin.client.accessor.render.AccessorShaderGroup).listFramebuffers

// Unused, but kept for consistency. Java equivalent used in Mixins
fun ViewFrustum.getRenderChunk(pos: BlockPos) = (this as calcite.client.mixin.client.accessor.render.AccessorViewFrustum).invokeGetRenderChunk(pos)
