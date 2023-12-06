public class PersonalIdentifier {
    private String personal_identifier;
    PersonalIdentifier(String personal_identifier){
	  this.personal_identifier = personal_identifier;
    }

	public void put_personal_identifier(String new_value) {
		personal_identifier = new_value;
	}

	public String get_personal_identifier() {
		return personal_identifier;
	}
}
