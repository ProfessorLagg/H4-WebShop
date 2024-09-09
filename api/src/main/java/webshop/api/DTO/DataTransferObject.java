package webshop.api.DTO;

public interface DataTransferObject<T> {
		public T toReal();

		public void fromReal(T object);
}
