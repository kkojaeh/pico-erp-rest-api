package pico.erp.restapi;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.actuate.autoconfigure.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.EndpointWebMvcAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.HealthIndicatorAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.ManagementServerPropertiesAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JndiConnectionFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.MultipartProperties;
import org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.ErrorHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import pico.erp.audit.AuditApi;
import pico.erp.bom.BomApi;
import pico.erp.comment.CommentApi;
import pico.erp.company.CompanyApi;
import pico.erp.facility.FacilityApi;
import pico.erp.invoice.InvoiceApi;
import pico.erp.item.ItemApi;
import pico.erp.notify.NotifyApi;
import pico.erp.order.acceptance.OrderAcceptanceApi;
import pico.erp.process.ProcessApi;
import pico.erp.project.ProjectApi;
import pico.erp.purchase.invoice.PurchaseInvoiceApi;
import pico.erp.purchase.order.PurchaseOrderApi;
import pico.erp.purchase.request.PurchaseRequestApi;
import pico.erp.quotation.QuotationApi;
import pico.erp.restapi.firebase.FirebaseAuthenticationProvider;
import pico.erp.restapi.firebase.FirebaseAuthenticationTokenFilter;
import pico.erp.restapi.sentry.SentryErrorHandler;
import pico.erp.restapi.web.CacheControlHandlerInterceptor;
import pico.erp.shared.ApplicationId;
import pico.erp.shared.ApplicationIntegrator;
import pico.erp.shared.ApplicationStarter;
import pico.erp.shared.Public;
import pico.erp.shared.SharedConfiguration;
import pico.erp.shared.impl.ApplicationImpl;
import pico.erp.shared.impl.ApplicationIntegratorImpl;
import pico.erp.shared.impl.DateConverter;
import pico.erp.shared.impl.LocalDateConverter;
import pico.erp.shared.impl.LocalTimeConverter;
import pico.erp.shared.impl.OffsetDateTimeConverter;
import pico.erp.user.UserApi;
import pico.erp.warehouse.WarehouseApi;
import pico.erp.work.schedule.WorkScheduleApi;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.schema.ResolvedTypes;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.schema.configuration.ObjectMapperConfigured;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.schema.ApiModelBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@SpringBootConfiguration
@ComponentScan
@EnableCaching
@EnableScheduling
@Configuration
@Import({
  SecurityAutoConfiguration.class,
  EmbeddedServletContainerAutoConfiguration.class,
  ServerPropertiesAutoConfiguration.class,
  EndpointWebMvcAutoConfiguration.class,
  HealthIndicatorAutoConfiguration.class,
  WebMvcAutoConfiguration.class,
  HttpMessageConvertersAutoConfiguration.class,
  ErrorMvcAutoConfiguration.class,
  PropertyPlaceholderAutoConfiguration.class,
  EndpointAutoConfiguration.class,
  EndpointWebMvcAutoConfiguration.class,
  ManagementServerPropertiesAutoConfiguration.class,
  DispatcherServletAutoConfiguration.class,
  SpringDataWebAutoConfiguration.class,
  MessageSourceAutoConfiguration.class,
  CacheAutoConfiguration.class,
  SharedConfiguration.class,
  JndiConnectionFactoryAutoConfiguration.class,
  ActiveMQAutoConfiguration.class,
  JmsAutoConfiguration.class,
})
public class RestApiApplication implements ApplicationStarter {

  public static SpringApplication application() {
    return new SpringApplicationBuilder(RestApiApplication.class)
      .web(true)
      .build();
  }

  private static void configurateObjectMapper(ObjectMapper mapper) {
    SimpleModule module = new SimpleModule("interface-implements", Version.unknownVersion());
    module.addSerializer(new StdSerializer<Stream<?>>(Stream.class, true) {
      @Override
      public void serialize(Stream<?> stream, JsonGenerator jgen, SerializerProvider provider)
        throws IOException {
        provider.findValueSerializer(Iterator.class, null)
          .serialize(stream.iterator(), jgen, provider);
      }
    });
    // SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();
    // module.setAbstractTypes(resolver);
    mapper.registerModule(module);
    mapper.registerModule(new JavaTimeModule());
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public static void main(String[] args) {
    ApplicationIntegrator integrator = new ApplicationIntegratorImpl();
    ServiceLoader<ApplicationStarter> loader = ServiceLoader.load(ApplicationStarter.class);
    List<ApplicationStarter> starters = new LinkedList<>();
    loader.forEach(starters::add);
    ApplicationStarter.sort(starters).forEach(starter -> integrator.add(starter.start(args)));
    integrator.integrate()
      .integrateMessageSource()
      .complete();
  }

  @Override
  public Set<ApplicationId> getDependencies() {
    return Stream.of(
      AuditApi.ID,
      UserApi.ID,
      CompanyApi.ID,
      ProcessApi.ID,
      ProjectApi.ID,
      QuotationApi.ID,
      ItemApi.ID,
      CommentApi.ID,
      FacilityApi.ID,
      WorkScheduleApi.ID,
      OrderAcceptanceApi.ID,
      WarehouseApi.ID,
      BomApi.ID,
      PurchaseRequestApi.ID,
      WarehouseApi.ID,
      PurchaseOrderApi.ID,
      NotifyApi.ID,
      InvoiceApi.ID,
      PurchaseInvoiceApi.ID
    ).collect(Collectors.toSet());
  }

  @Override
  public ApplicationId getId() {
    return ApplicationId.from("rest-api");
  }

  @Override
  public boolean isWeb() {
    return true;
  }

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    configurateObjectMapper(objectMapper);
    return objectMapper;
  }

  @Profile({"production", "development"})
  @Primary
  @Public
  @Bean
  public ErrorHandler sentryErrorHandler() {
    return new SentryErrorHandler();
  }

  @Override
  public pico.erp.shared.Application start(String... args) {
    return new ApplicationImpl(application().run(args));
  }

  @Configuration
  public static class FirebaseConfig {

    @Value("${firebase.service.account.key.location}")
    private Resource firebaseServiceAccountKeyFile;

    @Bean
    @SneakyThrows
    public FirebaseApp firebaseApp() {
      FirebaseOptions options = new FirebaseOptions.Builder()
        .setCredentials(
          GoogleCredentials.fromStream(firebaseServiceAccountKeyFile.getInputStream()))
        .build();

      return FirebaseApp.initializeApp(options);
    }

    @Bean
    public FirebaseAuth firebaseAuth() {
      return FirebaseAuth.getInstance(firebaseApp());
    }

  }

  @Configuration
  @EnableWebSecurity
  @EnableGlobalMethodSecurity(prePostEnabled = true)
  public static class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    @Override
    public AuthenticationManager authenticationManager() {
      return new ProviderManager(Collections.singletonList(authenticationProvider()));
    }

    @Bean
    public FirebaseAuthenticationProvider authenticationProvider() {
      return new FirebaseAuthenticationProvider();
    }

    @Bean
    public FirebaseAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
      FirebaseAuthenticationTokenFilter authenticationTokenFilter = new FirebaseAuthenticationTokenFilter();
      authenticationTokenFilter.setAuthenticationManager(authenticationManager());
      authenticationTokenFilter
        .setAuthenticationSuccessHandler((request, response, authentication) -> {
        });
      return authenticationTokenFilter;
    }

    @Override
    protected void configure(HttpSecurity s) throws Exception {
      // 캐시 컨트롤은 별로도 처리함
      s.headers().cacheControl().disable();

      s.csrf().disable();
      s.sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.NEVER);

      final CorsConfiguration configuration = new CorsConfiguration();
      configuration.setAllowedOrigins(ImmutableList.of("*"));
      configuration
        .setAllowedMethods(ImmutableList.of("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
      configuration.setAllowCredentials(true);
      configuration.setAllowedHeaders(
        ImmutableList.of("Authorization", "Cache-Control", "Content-Type", "X-Firebase-Auth",
          "X-Requested-With")
      );
      configuration.setMaxAge(3600L);

      final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", configuration);
      s.cors().configurationSource(source);
      s.addFilterBefore(authenticationTokenFilterBean(),
        UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
      web.ignoring()
        .antMatchers("/resources/**");
    }

    private static class NoVaryHttpServletResponseWrapper extends HttpServletResponseWrapper {

      /**
       * Constructs a response adaptor wrapping the given response.
       *
       * @param response The response map be wrapped
       * @throws IllegalArgumentException if the response is null
       */
      public NoVaryHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
      }

      @Override
      public void addHeader(String name, String value) {
        if (name.equalsIgnoreCase(HttpHeaders.VARY)) {
          return;
        }
        super.addHeader(name, value);
      }

      @Override
      public void setHeader(String name, String value) {
        if (name.equalsIgnoreCase(HttpHeaders.VARY)) {
          return;
        }
        super.setHeader(name, value);
      }
    }

  }

  @Profile("!production")
  @Configuration
  @EnableSwagger2
  public static class SwaggerConfig {

    @Bean
    public Docket api() {
      return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("pico.erp.restapi"))
        .paths(PathSelectors.any())
        .build();
    }


    /**
     * @JsonValue 로 지정된 클래스는 타입을 리턴 타입으로 변경
     */
    @Bean
    public ModelBuilderPlugin modelBuilderPlugin(TypeResolver typeResolver) {
      return new ApiModelBuilder(typeResolver) {
        @Override
        public void apply(ModelContext context) {
          super.apply(context);
          ResolvedType type = (ResolvedType) context.getType();
          Method jsonValueAccessor = type.getMemberMethods().stream()
            .map(method -> method.getRawMember())
            .filter(method -> method.getAnnotation(JsonValue.class) != null
              && method.getAnnotation(JsonValue.class).value() == true)
            .findFirst()
            .orElse(null);

          if (jsonValueAccessor != null) {
            context.getAlternateTypeProvider().addRule(
              AlternateTypeRules.newRule(
                typeResolver.resolve(type.getErasedType()),
                typeResolver.resolve(jsonValueAccessor.getReturnType()))
            );
          }
        }
      };
    }

    @Bean
    public ApplicationListener<ObjectMapperConfigured> objectMapperConfiguredListener() {
      return (event) -> configurateObjectMapper(event.getObjectMapper());
    }

    /**
     * Pageable 인터페이스 swagger 정의
     */
    @Bean
    public OperationBuilderPlugin pageableOperationBuilderPlugin(TypeNameExtractor nameExtractor,
      TypeResolver typeResolver) {
      ResolvedType pageableType = typeResolver.resolve(Pageable.class);
      return new OperationBuilderPlugin() {

        @Override
        public void apply(OperationContext context) {

          List<ResolvedMethodParameter> methodParameters = context.getParameters();
          List<Parameter> parameters = new ArrayList<>();

          for (ResolvedMethodParameter methodParameter : methodParameters) {
            ResolvedType resolvedType = methodParameter.getParameterType();

            if (pageableType.equals(resolvedType)) {
              ParameterContext parameterContext = new ParameterContext(methodParameter,
                new ParameterBuilder(),
                context.getDocumentationContext(),
                context.getGenericsNamingStrategy(),
                context);
              Function<ResolvedType, ? extends ModelReference> factory = createModelRefFactory(
                context.getGroupName(), parameterContext);

              ModelReference intModel = factory.apply(typeResolver.resolve(Integer.TYPE));
              ModelReference stringModel = factory
                .apply(typeResolver.resolve(List.class, String.class));

              parameters.add(new ParameterBuilder()
                .parameterType("query")
                .name("page")
                .modelRef(intModel)
                .description("Results page you want map retrieve (0..N)").build());
              parameters.add(new ParameterBuilder()
                .parameterType("query")
                .name("size")
                .modelRef(intModel)
                .description("Number of records per page").build());
              parameters.add(new ParameterBuilder()
                .parameterType("query")
                .name("sort")
                .modelRef(stringModel)
                .allowMultiple(true)
                .description("Sorting criteria in the format: property(,asc|desc). "
                  + "Default sort order is ascending. "
                  + "Multiple sort criteria are supported.")
                .build());
              context.operationBuilder().parameters(parameters);
            }
          }
        }

        private Function<ResolvedType, ? extends ModelReference> createModelRefFactory(
          String groupName,
          ParameterContext context) {
          ModelContext modelContext = ModelContext.inputParam(
            groupName,
            context.resolvedMethodParameter().getParameterType().getErasedType(),
            context.getDocumentationType(),
            context.getAlternateTypeProvider(),
            context.getGenericNamingStrategy(),
            context.getIgnorableParameterTypes());
          return ResolvedTypes.modelRefFactory(modelContext, nameExtractor);
        }

        @Override
        public boolean supports(DocumentationType delimiter) {
          return DocumentationType.SWAGGER_2.equals(delimiter);
        }

      };
    }

  }

  @Configuration
  @EnableWebMvc
  @EnableConfigurationProperties(MultipartProperties.class)
  public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MultipartProperties multipartProperties;

    @Override
    public void addFormatters(FormatterRegistry registry) {
      super.addFormatters(registry);
      registry.addConverter(new DateConverter());
      registry.addConverter(new OffsetDateTimeConverter());
      registry.addConverter(new LocalTimeConverter());
      registry.addConverter(new LocalDateConverter());


    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
      registry.addInterceptor(new CacheControlHandlerInterceptor());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
      registry.addResourceHandler("index.html")
        .addResourceLocations("classpath:/META-INF/resources/");

      registry.addResourceHandler("swagger-ui.html")
        .addResourceLocations("classpath:/META-INF/resources/");

      registry.addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");

    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
      registry.addRedirectViewController("/", "index.html");
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
      converters
        .stream()
        .filter(c -> c instanceof MappingJackson2HttpMessageConverter)
        .map(c -> (MappingJackson2HttpMessageConverter) c)
        .forEach(c -> c.setObjectMapper(objectMapper));
      super.extendMessageConverters(converters);
    }

    @Bean
    public MultipartResolver multipartResolver() {
      CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
      MultipartConfigElement config = multipartProperties.createMultipartConfig();

      multipartResolver.setMaxUploadSize(config.getMaxRequestSize());
      multipartResolver.setMaxUploadSizePerFile(config.getMaxFileSize());
      multipartResolver.setDefaultEncoding("UTF-8");
      return multipartResolver;
    }
  }

}
