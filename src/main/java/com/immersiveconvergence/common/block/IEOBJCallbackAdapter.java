package com.immersiveconvergence.common.block;

import com.immersiveconvergence.api.client.IICOBJModelCallback;

import blusunrize.immersiveengineering.client.models.IOBJModelCallback;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Optional;

public class IEOBJCallbackAdapter<T> implements IOBJModelCallback<T> {

    private final IICOBJModelCallback<T> delegate;

    public IEOBJCallbackAdapter(IICOBJModelCallback<T> delegate) { this.delegate = delegate; }

    @Override public TextureAtlasSprite getTextureReplacement(T object, String material) { return delegate.getTextureReplacement(object, material); }

    @Override public boolean shouldRenderGroup(T object, String group) { return delegate.shouldRenderGroup(object, group); }

    @Override public Optional<TRSRTransformation> applyTransformations(T object, String group, Optional<TRSRTransformation> transform) {
        return Optional.ofNullable(delegate.applyTransformations(object, group, transform.orElse(null)));
    }

    @Override public int getRenderColour(T object, String group) { return delegate.getRenderColour(object, group); }

    @Override public String getCacheKey(T object) { return delegate.getCacheKey(object); }

    @Override public boolean equals(Object other) {
        if (this == other) { return true; }
        if (!(other instanceof IEOBJCallbackAdapter)) { return false; }
        return delegate.equals(((IEOBJCallbackAdapter<?>)other).delegate);
    }

    @Override public int hashCode() { return delegate.hashCode(); }

    @Override public String toString() { return delegate.toString(); }
}
