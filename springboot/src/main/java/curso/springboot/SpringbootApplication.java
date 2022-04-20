package curso.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EntityScan(basePackages = "curso.springboot.model")//para o model se nao estiver funcionando 
@ComponentScan(basePackages= {"curso.*"}) //caso o controle nao pegue
@EnableJpaRepositories(basePackages ={"curso.springboot.repository"} ) //para o repository se nao estiver funcionando 
@EnableTransactionManagement //se a transaçao nao estiver funcionando 
@EnableWebMvc //abilitando os recursos do MVC da tela de login 
public class SpringbootApplication  implements WebMvcConfigurer{

	public static void main(String[] args) {
		SpringApplication.run(SpringbootApplication.class, args);
		
	}
	
	@Override //intercepitando/habilitando e redirecionando para a tela de login
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("/login");
		registry.setOrder(Ordered.LOWEST_PRECEDENCE);
	}

} 
//aqui criptograsfa a senha /criptografando a senha 123
//BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//String result = encoder.encode("8408");
//System.out.println(result);