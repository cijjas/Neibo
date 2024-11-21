package ar.edu.itba.paw.webapp.validation.validators.reference;

import ar.edu.itba.paw.interfaces.services.WorkerService;
import ar.edu.itba.paw.webapp.validation.constraints.reference.WorkerURNReferenceConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ar.edu.itba.paw.webapp.validation.ValidationUtils.extractId;

public class WorkerURNReferenceValidator implements ConstraintValidator<WorkerURNReferenceConstraint, String> {

    @Autowired
    private WorkerService workerService;

    @Override
    public void initialize(WorkerURNReferenceConstraint constraintAnnotation) {}

    @Override
    public boolean isValid(String workerURN, ConstraintValidatorContext context) {
        if (workerURN == null)
            return true;
        return workerService.findWorker(extractId(workerURN)).isPresent();
    }
}
