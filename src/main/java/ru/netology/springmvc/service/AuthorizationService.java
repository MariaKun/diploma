package ru.netology.springmvc.service;

//@Service
/*public class AuthorizationService {
    //@Autowired
    UserRepository userRepository;

    public List<Authorities> getAuthorities(UserEntity user) {
        if (isEmpty(user.getUserName()) || isEmpty(user.getPassword())) {
            throw new InvalidCredentials("User name or password is empty");
        }
        List<Authorities> userAuthorities = userRepository.getUserAuthorities(user);
        if (isEmpty(userAuthorities)) {
            throw new UnauthorizedUser("Unknown user " + user);
        }
        return userAuthorities;
    }

    private boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    private boolean isEmpty(List<?> str) {
        return str == null || str.isEmpty();
    }
}*/