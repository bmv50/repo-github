package Lesson_6;

import Lesson_6.db.dao.CategoriesMapper;
import Lesson_6.db.model.Categories;
import Lesson_6.dto.Category;
import Lesson_6.service.CategoryService;
import Lesson_6.util.DbUtils;
import Lesson_6.util.RetrofitUtils;
import io.qameta.allure.Feature;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Converter;
import retrofit2.Response;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;

import static Lesson_6.enums.CategoryType.FOOD;
import static org.assertj.core.api.Assertions.assertThat;

@Feature("Testing Categories API")
@DisplayName("Testing Categories API")
public class CategoryTests {
    static CategoryService categoryService;
    static CategoriesMapper categoriesMapper;
    public static Logger logger = LoggerFactory.getLogger(CategoryTests.class);

    @BeforeAll
    static void beforeAll() throws MalformedURLException {
        categoriesMapper = DbUtils.getCategoriesMapper();
        categoryService = RetrofitUtils.getRetrofit().create(CategoryService.class);
    }

    @DisplayName("Get Food Category (Positive Test)")
    @Test
    void getFoodCategoryPositiveTest() throws IOException {
        Response<Category> response = categoryService
                .getCategory((int) FOOD.id)
                .execute();

        Categories dbFood = categoriesMapper.selectByPrimaryKey((int)FOOD.id);

        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.body().getId()).as("Id is not equal to 1!").isEqualTo((long)dbFood.getId());
        assertThat(response.body().getTitle()).isEqualTo(dbFood.getTitle());

        RetrofitUtils.createRequestAttachment(response.raw().request().toString());
        RetrofitUtils.createResponseAttachment(response.raw().toString());
    }

    @DisplayName("Get Category Zero Id (Negative Test)")
    @Test
    void getCategoryZeroIdNegativeTest() throws IOException {
        Response<Category> response = categoryService
                .getCategory(0)
                .execute();

        assertThat(response.code()).isEqualTo(404);
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
            Converter<ResponseBody, Error> converter = RetrofitUtils.getRetrofit().responseBodyConverter(Error.class, new Annotation[0]);
            Error errorBody = converter.convert(body);
            assertThat(errorBody.getMessage()).isEqualTo("Unable to find category with id: 0");
        }

        RetrofitUtils.createRequestAttachment(response.raw().request().toString());
        RetrofitUtils.createResponseAttachment(response.raw().toString());
    }

    @DisplayName("Get Category Max Id (Negative Test)")
    @Test
    void getCategoryMaxIdNegativeTest() throws IOException {
        Response<Category> response = categoryService
                .getCategory((int) Long.MAX_VALUE)
                .execute();

        assertThat(response.code()).isEqualTo(404);
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
            Converter<ResponseBody, Error> converter = RetrofitUtils.getRetrofit().responseBodyConverter(Error.class, new Annotation[0]);
            Error errorBody = converter.convert(body);
            assertThat(errorBody.getMessage()).isEqualTo("Unable to find category with id: 9223372036854775807");
        }

        RetrofitUtils.createRequestAttachment(response.raw().request().toString());
        RetrofitUtils.createResponseAttachment(response.raw().toString());
    }

    @DisplayName("Get Category Min Id (Negative Test)")
    @Test
    void getCategoryMinIdNegativeTest() throws IOException {
        Response<Category> response = categoryService
                .getCategory((int) Long.MIN_VALUE)
                .execute();

        assertThat(response.code()).isEqualTo(404);
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
            Converter<ResponseBody, Error> converter = RetrofitUtils.getRetrofit().responseBodyConverter(Error.class, new Annotation[0]);
            Error errorBody = converter.convert(body);
            assertThat(errorBody.getMessage()).isEqualTo("Unable to find category with id: -9223372036854775808");
        }

        RetrofitUtils.createRequestAttachment(response.raw().request().toString());
        RetrofitUtils.createResponseAttachment(response.raw().toString());
    }

}