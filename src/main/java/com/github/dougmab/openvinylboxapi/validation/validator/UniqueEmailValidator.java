package com.github.dougmab.openvinylboxapi.validation.validator;

import com.github.dougmab.openvinylboxapi.entity.User;
import com.github.dougmab.openvinylboxapi.repository.UserRepository;
import com.github.dougmab.openvinylboxapi.validation.annotation.UniqueEmail;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private final HttpServletRequest request;

    private final UserRepository userRepository;

    @Autowired
    public UniqueEmailValidator(HttpServletRequest request, UserRepository userRepository) {
        this.request = request;
        this.userRepository = userRepository;
    }

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {}

    @Override
    @Transactional(readOnly = true)
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {

        @SuppressWarnings("unchecked")
        var uriVars = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        Long userId = uriVars.get("id") != null ? Long.parseLong(uriVars.get("id")) : null;

        User user = userRepository.findByEmail(email);

        if (user != null) {
            return user.getId().equals(userId);
        }

        return true;
    }

}
