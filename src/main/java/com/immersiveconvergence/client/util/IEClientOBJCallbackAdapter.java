package com.immersiveconvergence.client.util;

import com.immersiveconvergence.api.client.IICOBJModelCallback;
import com.immersiveconvergence.common.blocks.IEOBJCallbackAdapter;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class IEClientOBJCallbackAdapter<T> extends IEOBJCallbackAdapter<T> {
    private final IICOBJModelCallback<T> delegate;

    public IEClientOBJCallbackAdapter(IICOBJModelCallback<T> delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override public TextureAtlasSprite getTextureReplacement(T object, String material) { return delegate.getTextureReplacement(object, material); }
}
