package com.ning.hfind.util;

import java.util.Iterator;

public class PushbackIterator<T> implements Iterator<T>
{
    private final Iterator<T> delegate;
    private T last = null;
    private boolean pushedBack;

    public PushbackIterator(Iterator<T> delegate)
    {
        this.delegate = delegate;
    }

    public void pushBack()
    {
        pushedBack = true;
    }

    @Override
    public boolean hasNext()
    {
        return pushedBack || delegate.hasNext();
    }

    @Override
    public T next()
    {
        if (pushedBack) {
            pushedBack = false;

            return last;
        }

        last = delegate.next();

        return last;
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}