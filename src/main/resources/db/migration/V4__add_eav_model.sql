CREATE TABLE attributes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE TABLE attribute_values (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    attribute_id BIGINT NOT NULL,
    value VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT fk_attribute_value_attribute FOREIGN KEY (attribute_id) REFERENCES attributes(id) ON DELETE CASCADE
);

CREATE TABLE variant_attribute_values (
    variant_id BIGINT NOT NULL,
    attribute_value_id BIGINT NOT NULL,
    PRIMARY KEY (variant_id, attribute_value_id),
    CONSTRAINT fk_variant_attribute_variant FOREIGN KEY (variant_id) REFERENCES product_variants(id) ON DELETE CASCADE,
    CONSTRAINT fk_variant_attribute_value FOREIGN KEY (attribute_value_id) REFERENCES attribute_values(id) ON DELETE CASCADE
);
