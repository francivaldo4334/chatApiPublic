package br.com.confchat.api.enums;

public enum RS {
    a_email_has_been_send_to("string.email.has.been.send.to"),
    user_active("user.active"),
    verification_code_not_found("verification.code.not.found"),
    user_actived("user.actived"), 
    the_password_must_have_at_least_15_characters_a_number_and_a_special_character("the.password.must.have.at.least.15.characters.a.number.and.a.special.character"), 
    email_already_registered("email.already.registered"),
    existing_user("existing.user"),
    loginOrEmail_must_have_only_numbers_and_letters_and_at_least_8_charactes("loginOrEmail.must.have.only.numbers.and.letters.and.at.least.8.charactes"),
    email_must_have_valid_format("email.must.have.valid.format"),
    User_must_by_at_least_18_years_old("User.must.by.at.least.18.years.old"),
    the_name_must_have_at_first_and_last_name("the.name.must.have.at.first.and.last.name"),
    success("success"), success_logout("success.logout"), unknown("unknown"), register_error("register.error"), criptographic_error("criptographic.error");

    private String nome;

    RS(String nome) {
        this.nome = nome;
    }

    public String get() {
        return nome;
    }
}
