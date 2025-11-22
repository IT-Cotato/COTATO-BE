package org.cotato.csquiz.migration;

public interface MigrationJob {

	void migrate();

	default String getName() {
		return getClass().getSimpleName();
	}
}
