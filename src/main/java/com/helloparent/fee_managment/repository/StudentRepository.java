
package com.helloparent.fee_managment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.helloparent.fee_managment.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

	boolean existsByEmail(String email);

	@Query("SELECT s FROM Student s WHERE s.id = :id")
	java.util.Optional<Student> findByIdForUpdate(@Param("id") Long id);

	Page<Student> findByActiveTrue(Pageable pageable);

	@Query("""
			SELECT s FROM Student s
			WHERE s.active = true
			AND (
			    LOWER(s.firstName) LIKE LOWER(CONCAT('%', :name, '%'))
			    OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :name, '%'))
			)
			""")
	Page<Student> searchActiveStudentsByName(@Param("name") String name, Pageable pageable);
}
