package myjdbceg;

import javax.servlet.http.HttpServlet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class Application {
	
	@Bean
	   public ServletRegistrationBean axisServletRegistrationBean() {
	      ServletRegistrationBean registration = new ServletRegistrationBean(new MyServlet(), "/services");
	     // registration.addUrlMappings("*.jws");
	      return registration;
	   }
	@Bean
	public HttpServlet foo() {
	    return new MyServlet();
	}
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

	}

}
