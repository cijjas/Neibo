package ar.edu.itba.paw.webapp.validation.validators.specific;

import ar.edu.itba.paw.interfaces.services.WorkerService;
import ar.edu.itba.paw.webapp.validation.constraints.specific.WorkerIdConstraint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class WorkerIdValidator implements ConstraintValidator<WorkerIdConstraint, Long> {

    @Autowired
    private WorkerService workerService;

    @Override
    public void initialize(WorkerIdConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(Long workerId, ConstraintValidatorContext context) {
        if (workerId == null)
            return false;
        if (workerId < 1)
            return false;
        return workerService.findWorker(workerId).isPresent();
    }
}
