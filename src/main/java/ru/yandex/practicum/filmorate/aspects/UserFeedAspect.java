package ru.yandex.practicum.filmorate.aspects;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.annotation.SaveUserFeed;
import ru.yandex.practicum.filmorate.exceptions.UserFeedFieldNotFoundException;
import ru.yandex.practicum.filmorate.model.UserFeed;
import ru.yandex.practicum.filmorate.storage.UserFeedStorage;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class UserFeedAspect {
    @Pointcut("@annotation(ru.yandex.practicum.filmorate.annotation.SaveUserFeed)")
    public void pointcut() {
    }

    private final UserFeedStorage userFeedStorage;

    @AfterReturning("pointcut()")
    public void saveFeed(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        SaveUserFeed annotation = method.getAnnotation(SaveUserFeed.class);

        UserFeed userFeed = UserFeed.builder()
                .eventType(annotation.value())
                .operation(annotation.operation())
                .timestamp(System.currentTimeMillis())
                .build();

        String className = joinPoint.getClass().getName();
        Object[] args = joinPoint.getArgs();
        String[] params = ((MethodSignature) joinPoint.getSignature()).getParameterNames();

        Long userId = getFieldValueByName(annotation.userIdPropertyName(), args, params, className);
        Long entityId = getFieldValueByName(annotation.entityIdPropertyName(), args, params, className);
        userFeed.setUserId(userId);
        userFeed.setEntityId(entityId);
        userFeedStorage.save(userFeed);
    }

    private Long getFieldValueByName(String fieldName, Object[] args, String[] params, String className) {
        for (int i = 0; i < args.length; i++) {
            if (params[i].equals(fieldName)) {
                return (Long) args[i];
            }
        }

        for (Object arg : args) {
            ExpressionParser expressionParser = new SpelExpressionParser();
            Expression expression = expressionParser.parseExpression(fieldName);
            EvaluationContext context = new StandardEvaluationContext(arg);
            try {
                return (Long) expression.getValue(context);
            } catch (EvaluationException ignored) {
            }
        }
        throw new UserFeedFieldNotFoundException(String.format(
                "Unable to find field with name %s in instance of class: %s", fieldName, className
        ));
    }
}
