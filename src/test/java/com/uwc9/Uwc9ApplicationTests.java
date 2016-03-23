package com.uwc9;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;

import static java.util.Collections.EMPTY_MAP;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Uwc9Application.class)
@WebAppConfiguration
public class Uwc9ApplicationTests {

	private final static Logger logger = Logger.getLogger(Uwc9ApplicationTests.class);

	@Autowired
	GitHttpRequest request;

	@Autowired
	GitHubProperties properties;

	@Autowired
	StarsFabric fabrica;

	@Test
	public void contextLoads() throws Exception {
		String repo = "docker-library/php";

		GitRepo starsInfo = fabrica.getPeriodStarsInfo(Period.WEEK, repo);
		System.out.println(starsInfo.toString());
		logger.info(starsInfo.getStarsInfo().keySet().size());
		System.out.println("non parse stars" + starsInfo.getNonParseStars());

		Assert.assertTrue(starsInfo.getStarsInfo().keySet().size() > 0);
	}

}
