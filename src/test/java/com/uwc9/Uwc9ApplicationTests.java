package com.uwc9;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Uwc9Application.class)
@WebAppConfiguration
public class Uwc9ApplicationTests {

	private final static Logger logger = Logger.getLogger(Uwc9ApplicationTests.class);

	@Autowired
	GitHttpRequest request;

	@Autowired
	StarsFabrica fabrica;

	@Test
	public void contextLoads() throws Exception {
		String repo = "5BRAINS/resumein";

		Response r = request.sendGet(request.getStarsRequestUrl.apply(repo), Collections.EMPTY_MAP, Collections.singletonMap("Accept", "application/vnd.github.v3.star+json"));

		GitRepo gitRepo = new GitRepo(repo);

		fabrica.updateRepoInfo(r, gitRepo);
		for (Date d : gitRepo.getStarsInfo().keySet()){
			logger.info(d);
			logger.info(gitRepo.getStarsInfo().get(d));
		}
		Assert.assertTrue(gitRepo.getStarsInfo().keySet().size() != 0);

	}

}
