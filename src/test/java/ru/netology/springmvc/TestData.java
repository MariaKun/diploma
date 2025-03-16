package ru.netology.springmvc;

import org.springframework.mock.web.MockMultipartFile;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import ru.netology.springmvc.entity.Role;
import ru.netology.springmvc.entity.User;

import java.util.random.RandomGenerator;

public class TestData {

    public static long testUserId = 1L;

    public static String headerName = "auth-token";

    public static String fileUri = "/file";
    public static String filenameParam = "filename";
    public static String fileControlName = "file";

    public static String listUri = "/list";
    public static String limitParam = "limit";

    public static String signupUri = "/signup";
    public static String loginUri = "/login";

    public static String randomFileName() {
        return RandomStringUtils.randomAlphabetic(5) + ".txt";
    }

    public static long randomUserId() {
        return RandomGenerator.getDefault().nextLong(100L, 200L);
    }

    public static MockMultipartFile randomFile() {
        String filename = randomFileName();
        return new MockMultipartFile(filename, filename, "text/plain", "text".getBytes());
    }

    public static User randomUser() {
        return new User(
                randomUserId(),
                RandomStringUtils.randomAlphabetic(5),
                RandomStringUtils.randomAlphabetic(5),
                RandomStringUtils.randomAlphabetic(5) + "@test.com",
                Role.ROLE_USER
        );
    }
}
