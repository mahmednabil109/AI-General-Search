package code.datastructure;

import java.util.Stack;

public class GStack<T> implements GenericQueue<T>{

    private Stack<T> _stack;
    public GStack(){
        this._stack = new Stack<>();
    }

    @Override
    public T removeFront() {
        return this._stack.pop();
    }

    @Override
    public void add(T t) {
        this._stack.add(t);
    }

    @Override
    public boolean isEmpty() {
        return this._stack.isEmpty();
    }
}
