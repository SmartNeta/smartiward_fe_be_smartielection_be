package com.mnt.sampark.modules.mdm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnt.sampark.core.db.model.LongIdBaseEntity;
import com.mnt.sampark.core.generator.CSVServiceGenerator;
import com.mnt.sampark.modules.mdm.tools.MdmFactory;
import com.mnt.sampark.modules.mdm.tools.SpecDto;
import com.mnt.sampark.modules.mdm.tools.TSpecification;
import com.mnt.sampark.mvc.utils.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

//import org.springframework.validation.Validator;

@Controller
@RequestMapping("/open/sampark")
@CrossOrigin(origins = "*")
public class MDMController {

    @Autowired
    Validator validator;// = Validation.buildDefaultValidatorFactory().getValidator();

    @Autowired
    MdmFactory factory;

    @Autowired
    CSVServiceGenerator csvServiceGenerator;

    @PostMapping(value = "/api/save/{classType}")
    @ResponseBody
    public HashMap<String, Object> save(HttpEntity<String> httpEntity, @PathVariable String classType) {
        ResponseWrapper wrapper = new ResponseWrapper();

        try {
            String jsonBody = httpEntity.getBody();
            ObjectMapper objectmapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            Class<? extends LongIdBaseEntity> domainClass;
            if ("SecurityGroup".equals(classType) || "Account".equals(classType) || "User".equals(classType) || "UserDetail".equals(classType) || "SecurityActions".equals(classType)) {
                domainClass = (Class<? extends LongIdBaseEntity>) Class.forName("com.mnt.sampark.core.db.model." + classType);
            } else {
                domainClass = (Class<? extends LongIdBaseEntity>) Class.forName("com.mnt.sampark.modules.mdm.db.domain." + classType);
            }
            LongIdBaseEntity obj = objectmapper.readValue(jsonBody, domainClass);
            Set<ConstraintViolation<Object>> violations = validator.validate(obj);
            if (violations.isEmpty()) {
                wrapper.asData(factory.save(obj, domainClass))
                        .asMessage(ResponseWrapper.MSG_SAVED_SUCCESSFULLY).asCode("201");
            } else {
                violations.forEach(voilation -> {
                    wrapper.addAsVoilation(voilation.getPropertyPath().toString(), voilation.getMessage());
                }
                );
                wrapper.asCode("422");
            }
        }
        catch (DataIntegrityViolationException ex) {
            wrapper.asCode("11540");
            wrapper.asMessage(ex.getMessage());
            ex.printStackTrace();
        }
        catch (Exception ex) {
            wrapper.asCode("500");
            wrapper.asMessage(ex.getMessage());
            ex.printStackTrace();
        }

        return wrapper.get();
    }

    @PutMapping(value = "/api/save/{classType}/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public HashMap<String, Object> update(HttpEntity<String> httpEntity, @PathVariable String classType, @PathVariable Long id) {
        ResponseWrapper wrapper = new ResponseWrapper();

        try {
            Class<? extends LongIdBaseEntity> domainClass;
            if ("SecurityGroup".equals(classType) || "Account".equals(classType) || "User".equals(classType)|| "UserDetail".equals(classType) || "SecurityActions".equals(classType)) {
                domainClass = (Class<? extends LongIdBaseEntity>) Class.forName("com.mnt.sampark.core.db.model." + classType);
            } else {
                domainClass = (Class<? extends LongIdBaseEntity>) Class.forName("com.mnt.sampark.modules.mdm.db.domain." + classType);
            }

            String jsonBody = httpEntity.getBody();
            ObjectMapper objectmapper = new ObjectMapper();
            LongIdBaseEntity obj = objectmapper.readValue(jsonBody, domainClass);
            if (factory.isIdPresent(domainClass, id)) {
                obj.setId(id);
                Set<ConstraintViolation<Object>> violations = validator.validate(obj);
                if (violations.isEmpty()) {
                    wrapper.asData(factory.save(obj, domainClass))
                            .asMessage(ResponseWrapper.MSG_SAVED_SUCCESSFULLY).asCode("201");
                } else {
                    violations.forEach(voilation -> {
                        wrapper.addAsVoilation(voilation.getPropertyPath().toString(), voilation.getMessage());
                    }
                    );
                    wrapper.asCode("422");
                }
            } else {
                wrapper.asCode("404");
                wrapper.asMessage("Entity does not exist");
            }

        } catch (Exception ex) {
            wrapper.asCode("500");
            wrapper.asMessage(ex.getMessage());
            ex.printStackTrace();
        }

        return wrapper.get();
    }

    @DeleteMapping(value = "/api/delete/{classType}/{id}")
    @ResponseBody
    public HashMap<String, Object> delete(@PathVariable String classType, @PathVariable Long id) {
        ResponseWrapper wrapper = new ResponseWrapper();
        try {
            Class<? extends LongIdBaseEntity> domainClass;
            if ("SecurityGroup".equals(classType) || "Account".equals(classType) || "User".equals(classType) || "UserDetail".equals(classType) || "SecurityActions".equals(classType)) {
                domainClass = (Class<? extends LongIdBaseEntity>) Class.forName("com.mnt.sampark.core.db.model." + classType);
            } else {
                domainClass = (Class<? extends LongIdBaseEntity>) Class.forName("com.mnt.sampark.modules.mdm.db.domain." + classType);
            }
            if (factory.isIdPresent(domainClass, id)) {
                factory.delete(domainClass, id);
                wrapper.asData(classType + " Deleted Successfully.")
                        .asMessage(ResponseWrapper.MSG_DELETED_SUCCESSFULLY).asCode("201");
                wrapper.asCode("201");
            } else {
                wrapper.asCode("404");
                wrapper.asMessage("Entity does not exist");
            }
        } catch (DataIntegrityViolationException ex){
            wrapper.asCode("320");
            wrapper.asMessage(ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            wrapper.asCode("500");
            wrapper.asMessage(ex.getMessage());
            ex.printStackTrace();
        }

        return wrapper.get();
    }


    @RequestMapping(value = "/api/grid/{classType}", method = {RequestMethod.GET})
    @ResponseBody
    public ResponseWrapper gridData(@PathVariable String classType) {
        ResponseWrapper wrapper = new ResponseWrapper();
        try {
            Class<? extends LongIdBaseEntity> domainClass;
            if ("SecurityGroup".equals(classType) || "Account".equals(classType) || "User".equals(classType)|| "UserDetail".equals(classType) || "SecurityActions".equals(classType)) {
                domainClass = (Class<? extends LongIdBaseEntity>) Class.forName("com.mnt.sampark.core.db.model." + classType);
            } else {
                domainClass = (Class<? extends LongIdBaseEntity>) Class.forName("com.mnt.sampark.modules.mdm.db.domain." + classType);
            }
            wrapper.asData(factory.findAll(domainClass));
        } catch (Exception ex) {
            wrapper.asCode("500");
            wrapper.asMessage(ex.getMessage());
            ex.printStackTrace();
        }
        return wrapper;
    }

    @PostMapping(value = "/api/query/{classType}")
    @ResponseBody
    public HashMap<String, Object> queryState(@PathVariable String classType, @RequestBody List<SpecDto> specs) {
        ResponseWrapper wrapper = new ResponseWrapper();
        try {
            Class<? extends LongIdBaseEntity> domainClass;
            if ("SecurityGroup".equals(classType) || "Account".equals(classType) || "UserAccount".equals(classType)) {
                domainClass = (Class<? extends LongIdBaseEntity>) Class.forName("com.mnt.sampark.core.db.model." + classType);
            } else {
                domainClass = (Class<? extends LongIdBaseEntity>) Class.forName("com.mnt.sampark.modules.mdm.db.domain." + classType);
            }
            Specification specification = new TSpecification(specs);
            wrapper.asData(factory.query(domainClass, specification));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return wrapper.get();
    }

}
