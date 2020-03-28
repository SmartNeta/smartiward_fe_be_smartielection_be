package com.mnt.sampark.modules.mdm.tools;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

public class TSpecification implements Specification {

	List<SpecDto> specs;
	public TSpecification(List<SpecDto> specs) {
		this.specs = specs;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate toPredicate(Root root, CriteriaQuery cq, CriteriaBuilder cb) {
		Predicate p = cb.conjunction();
		specs.forEach(spec -> {
			 
			 
			 if(spec.path.contains(".")) {
				Join join = root.join(spec.path.split("\\.")[0]);
				p.getExpressions().add(cb.equal(join.get(spec.path.split("\\.")[1]), spec.value));
			 } else {
				 if(spec.op.equals("eq")) {
					 p.getExpressions().add(cb.equal(root.get(spec.path), spec.value));
				 }
				 
				 if(spec.op.equals("like")) {
					 p.getExpressions().add(cb.like(root.get(spec.path), spec.value));
				 }
				 //{"path":"code","op":"in","value":"PN,ASR"}
				 if(spec.op.equals("in")) {
					 p.getExpressions().add((root.get(spec.path).in(spec.value.split(","))));
				 }
			 }
			
		});
		 
		return p;
	}

}
