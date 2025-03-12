package ru.netology.springmvc;


/**
 * Конфигуратор , позволяющий разделить обьект User и получить из него login и password
 */
/*@Configuration
public class ApplicationWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new userArgumentResolver());
    }

    private class userArgumentResolver implements HandlerMethodArgumentResolver {

        @Override
        public boolean supportsParameter(MethodParameter methodParameter) {
            return methodParameter.getParameterType().isAssignableFrom(UserEntity.class);
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
            String login = webRequest.getParameter("user");
            String password = webRequest.getParameter("password");
            return new UserEntity().builder().userName(login).password(password).build();
        }
    }
}*/