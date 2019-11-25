package yiyuan.config;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                //.apis(RequestHandlerSelectors.basePackage("hr01.controller"))
                .apis(basePackage("yiyuan"))
                .paths(PathSelectors.any())
                .build().apiInfo(
                        new ApiInfoBuilder().description("hr 测试接口文档").build()
                );

    }


    private static Predicate<RequestHandler> basePackage(final String basePackage) {
        return input -> declaringClass(input).transform(handlerPackage(basePackage)).or(true);
    }

    private static Optional<? extends Class<?>> declaringClass(RequestHandler input) {
        return Optional.fromNullable(input.declaringClass());
    }

    //重写handlerPackage，实现多包设置，以“ ; ” 隔开
    private static Function<Class<?>, Boolean> handlerPackage(final String basePackages) {
        return input -> {
            for (String basePackage : basePackages.split(";")) {  //分号分割
                if (ClassUtils.getPackageName(input).startsWith(basePackage)) {
                    return true;
                }
            }

            return false;
        };
    }

}

