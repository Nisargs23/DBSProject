
CREATE OR REPLACE FUNCTION add_customer(
    v_name  IN varchar,
    v_email IN varchar,
    v_phone IN number
) RETURN INTEGER IS
RETURN_VAL INTEGER:=1;
BEGIN
BEGIN
    INSERT INTO customer
    VALUES ('C0000',v_name, v_phone, v_email);
EXCEPTION
    WHEN OTHERS THEN
        RETURN_VAL := 0;
END;
RETURN RETURN_VAL;
END;
/


CREATE OR REPLACE FUNCTION add_supplier(
    v_name           IN VARCHAR2,
    v_phone          IN NUMBER,
    v_email          IN VARCHAR2,
    v_address_line1  IN VARCHAR,
    v_city           IN VARCHAR,
    v_state          IN VARCHAR,
    v_pincode        IN NUMBER,
    v_country        IN VARCHAR
) RETURN INTEGER IS
    v_next_address_id CHAR;
    return_val INTEGER := 1;
BEGIN
BEGIN
    SELECT next_addressid INTO v_next_address_id FROM reference_variables;

    INSERT INTO address
    VALUES (v_next_address_id, v_address_line1, v_city, v_state, v_pincode, v_country);

    INSERT INTO supplier
    VALUES (v_name, v_phone, v_email, v_next_address_id);

    UPDATE reference_variables SET next_addressid = TRIM('A'|| TO_CHAR(TO_NUMBER(SUBSTR(v_next_address_id,2,4))+1,'FM0000'));
EXCEPTION
    WHEN OTHERS THEN
        return_val := 0;
END;
        RETURN return_val; 
END;
/



CREATE OR REPLACE FUNCTION add_manufacturer(
    v_name            IN VARCHAR,
    v_phone           IN NUMBER,
    v_email           IN VARCHAR,
    v_address_line1   IN VARCHAR,
    v_city            IN VARCHAR,
    v_state           IN VARCHAR,
    v_pincode         IN NUMBER,
    v_country         IN VARCHAR
) RETURN BOOLEAN IS
    v_next_address_id reference_variables.next_addressid%TYPE;
    return_val BOOLEAN := TRUE;
BEGIN
BEGIN
    SELECT next_addressid INTO v_next_address_id FROM reference_variables;

    INSERT INTO address
    VALUES (v_next_address_id, v_address_line1, v_city, v_state, v_pincode, v_country);

    INSERT INTO manufacturer (name, phone, email, base_location_address_id)
    VALUES (v_name, v_phone, v_email, v_next_address_id);
    
    UPDATE reference_variables SET next_addressid = 'A'|| TO_CHAR(TO_NUMBER(SUBSTR(v_next_address_id,2,4))+1,'FM0000');
EXCEPTION
    WHEN OTHERS THEN
        return_val := FALSE;
END;
   RETURN return_val; 
END add_manufacturer;
/




CREATE OR REPLACE FUNCTION add_employee(
    v_first_name      IN VARCHAR,
    v_middle_name     IN VARCHAR,
    v_last_name       IN VARCHAR,
    v_sex             IN CHAR,
    v_phone           IN NUMBER,
    v_email           IN VARCHAR,
    v_address_line1   IN VARCHAR,
    v_city            IN VARCHAR,
    v_state           IN VARCHAR,
    v_pincode         IN NUMBER,
    v_country         IN VARCHAR
) RETURN INTEGER IS
    v_next_address_id reference_variables.next_addressid%TYPE;
    return_val INTEGER := 1;
BEGIN
BEGIN
    SELECT next_addressid INTO v_next_address_id FROM reference_variables;

    INSERT INTO address
    VALUES (v_next_address_id, v_address_line1, v_city, v_state, v_pincode, v_country);

    INSERT INTO employee
    VALUES ('E00',v_first_name, v_middle_name, v_last_name, v_sex, v_phone, v_email, v_next_address_id);
    
    UPDATE reference_variables SET next_addressid = 'A'|| TO_CHAR(TO_NUMBER(SUBSTR(v_next_address_id,2,4))+1,'FM0000');

    EXCEPTION
    WHEN OTHERS THEN
        return_val := 0;
END;
        RETURN return_val; 
END add_employee;
/



CREATE OR REPLACE FUNCTION add_online_partner(
    v_name                 IN VARCHAR,
    v_commission_percent   IN NUMBER
) RETURN INTEGER IS
    return_val INTEGER := 1;
BEGIN
BEGIN
    INSERT INTO online_partners
    VALUES (v_name, v_commission_percent);

EXCEPTION
    WHEN OTHERS THEN
        return_val := 0;
END;
        RETURN return_val;
END add_online_partner;
/



CREATE OR REPLACE FUNCTION add_item(
    v_batch_no       IN VARCHAR,
    v_item_name      IN VARCHAR,
    v_mfd            IN DATE,
    v_exp_date       IN DATE,
    v_cost_price     IN NUMBER,
    v_selling_price  IN NUMBER,
    v_stock          IN NUMBER,
    v_manufacturer   IN VARCHAR
) RETURN BOOLEAN IS
    return_val BOOLEAN := TRUE;
BEGIN
BEGIN    
    INSERT INTO item
    VALUES (v_batch_no, v_item_name, v_mfd, v_exp_date, v_cost_price, v_selling_price, v_stock, v_manufacturer);

EXCEPTION
    WHEN OTHERS THEN
        return_val := FALSE;
END;
        RETURN return_val;
END add_item;
/

CREATE OR REPLACE FUNCTION FIND_CUSTOMER_ID( V_PHONE NUMBER )
RETURN VARCHAR IS
C_ID CUSTOMER.ID%TYPE;
BEGIN
SELECT ID INTO C_ID FROM CUSTOMER WHERE PHONE = V_PHONE;
IF C_ID IS NULL THEN C_ID := 'C0000';
END IF;
RETURN C_ID;
END;
/

CREATE OR REPLACE FUNCTION ADD_ITEM_ORDERED(
ORDERID IN CHAR,
BATCH_NO