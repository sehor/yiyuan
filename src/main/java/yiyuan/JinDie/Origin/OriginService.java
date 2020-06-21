package yiyuan.JinDie.Origin;

import java.time.LocalDate;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

public interface OriginService {
	@CacheEvict(value = "origin", allEntries = true)
	Origin addOrigin(Origin origin);

	@Cacheable("origin")
	Origin getOrigin(String id);

	@CacheEvict(value = "origin", allEntries = true)
	Origin updateOrigin(Origin origin);

	@CacheEvict(value = "origin", allEntries = true)
	void deleteOrigin(Origin origin);

	@CacheEvict(value = "origin", allEntries = true)
	void deleteOrigin(String id);

	//@Cacheable("origin")
	List<Origin> getAll();

	@CacheEvict(value = "origin", allEntries = true)
	List<Origin> saveAll(List<Origin> origins);

	//@Cacheable(value="origin")
	List<Origin> getInPeriod(String companyName, LocalDate begin, LocalDate end);

	@CacheEvict(value = "origin", allEntries = true)
	List<Origin> deleteByCompanyName(String companyName);
}