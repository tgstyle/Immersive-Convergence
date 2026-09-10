package com.immersiveconvergence.api.client;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;


@SuppressWarnings("unused")
public interface IICOBJModelCallback<T> {

    IUnlistedProperty<IICOBJModelCallback<?>> PROPERTY = new IUnlistedProperty<IICOBJModelCallback<?>>() {
        @Override public String getName() { return "ic_obj_model_callback"; }

        @Override public boolean isValid(IICOBJModelCallback<?> value) { return true; }

        @SuppressWarnings("unchecked")
        @Override public Class<IICOBJModelCallback<?>> getType() { return (Class<IICOBJModelCallback<?>>)(Class<?>)IICOBJModelCallback.class; }

        @Override public String valueToString(IICOBJModelCallback<?> value) { return value.toString(); }
    };

    @SideOnly(Side.CLIENT)
    default TextureAtlasSprite getTextureReplacement(T object, String material) { return null; }

    @SideOnly(Side.CLIENT)
    default boolean shouldRenderGroup(T object, String group) { return true; }

    @SideOnly(Side.CLIENT)
    @Nullable default TRSRTransformation applyTransformations(T object, String group, @Nullable TRSRTransformation transform) { return transform; }

    @SideOnly(Side.CLIENT)
    default int getRenderColour(T object, String group) { return 0xffffffff; }

    @SideOnly(Side.CLIENT)
    default String getCacheKey(T object) { return null; }
}
