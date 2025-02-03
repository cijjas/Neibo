#!/bin/bash

cd ./app/shared/components || exit
find . -name "*.component.ts" -type f | while read -r componentFile; do
    baseFile="${componentFile%.ts}"
    specFile="${baseFile}.spec.ts"

    if [ ! -f "$specFile" ]; then
        echo "Generating spec for: $componentFile"
        filename=$(basename "$componentFile")
        nameWithoutExt="${filename%.component.ts}"
        
        # Convert kebab-case to PascalCase (e.g., admin-amenities-page → AdminAmenitiesPageComponent)
        componentName=$(echo "$nameWithoutExt" | awk -F'-' '{for (i=1; i<=NF; i++) $i=toupper(substr($i,1,1)) substr($i,2)}1' OFS='')Component

        cat > "$specFile" <<EOF
import { ${componentName} } from './${nameWithoutExt}.component';
import { ComponentFixture, TestBed } from '@angular/core/testing';

describe('${componentName}', () => {
  let component: ${componentName};
  let fixture: ComponentFixture<${componentName}>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ${componentName} ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(${componentName});
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
EOF

    else
        echo "Spec already exists for: $componentFile"
    fi
done