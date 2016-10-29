package me.ranol.serverisalive.utils;

public class Result<T> {
	T obj;

	public static <T> Result<T> create() {
		return new Result<T>();
	}

	@SuppressWarnings("unchecked")
	public static <T> Result<T>[] array(int size) {
		Result<T>[] array = new Result[size];
		fill(array);
		return array;
	}

	public static <T> void fill(Result<T>[] target) {
		for (int i = 0; i < target.length; i++) {
			target[i] = create();
		}
	}

	public T get() {
		return obj;
	}

	public void set(T t) {
		obj = t;
	}

	@Override
	public String toString() {
		return obj.toString();
	}
}
