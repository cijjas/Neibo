package ar.edu.itba.paw.webapp.validation.validators.urn;

import ar.edu.itba.paw.interfaces.services.WorkerService;
import ar.edu.itba.paw.webapp.validation.URNValidator;
import ar.edu.itba.paw.webapp.validation.constraints.form.WorkerURNConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ExtractionUtils.extractFirstId;

public class WorkerURNValidator implements ConstraintValidator<WorkerURNConstraint, String> {

    @Autowired
    private WorkerService workerService;

    @Override
    public void initialize(WorkerURNConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String workerURN, ConstraintValidatorContext context) {
        if (workerURN == null)
            return true;
        if (!URNValidator.validateURN(workerURN, "workers"))
            return false;
        return workerService.findWorker(extractFirstId(workerURN)).isPresent();
    }
}
