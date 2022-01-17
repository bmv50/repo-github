package Lesson_6.service;
import Lesson_6.dto.Category;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CategoryService {

    @GET ("categories/{id}")
    Call<Category> getCategory (@Path ("id") int id);
}
