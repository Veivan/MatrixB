package service;

public class GenderChecker {
	private static String first_name;
	private static String surname;

	public static enum Gender {
		FEMALE, MALE, NEUTRAL
	};

	private static String[][] surname_completions = {
			{ "ова", "ева", "ина", "ая", "яя", "екая", "цкая" },
			{ "ов", "ев", "ин", "ын", "ой", "цкий", "ский", "цкой", "ской" } };

	private static String[][] names = {
			{ "авдотья", "аврора", "агата", "агния", "агриппина", "ада",
					"аксинья", "алевтина", "александра", "алёна", "алена",
					"алина", "алиса", "алла", "альбина", "амалия", "анастасия",
					"ангелина", "анжела", "анжелика", "анна", "антонина",
					"анфиса", "арина", "белла", "божена", "валентина",
					"валерия", "ванда", "варвара", "василина", "василиса",
					"вера", "вероника", "виктория", "виола", "виолетта",
					"вита", "виталия", "владислава", "власта", "галина",
					"глафира", "дарья", "диана", "дина", "ева", "евгения",
					"евдокия", "евлампия", "екатерина", "елена", "елизавета",
					"ефросиния", "ефросинья", "жанна", "зиновия", "злата",
					"зоя", "ивонна", "изольда", "илона", "инга", "инесса",
					"инна", "ирина", "ия", "капитолина", "карина", "каролина",
					"кира", "клавдия", "клара", "клеопатра", "кристина",
					"ксения", "лада", "лариса", "лиана", "лидия", "лилия",
					"лина", "лия", "лора", "любава", "любовь", "людмила",
					"майя", "маргарита", "марианна", "мариетта", "марина",
					"мария", "марья", "марта", "марфа", "марьяна", "матрёна",
					"матрена", "матрона", "милена", "милослава", "мирослава",
					"муза", "надежда", "настасия", "настасья", "наталия",
					"наталья", "нелли", "ника", "нина", "нинель", "нонна",
					"оксана", "олимпиада", "ольга", "пелагея", "полина",
					"прасковья", "раиса", "рената", "римма", "роза", "роксана",
					"руфь", "сарра", "светлана", "серафима", "снежана",
					"софья", "софия", "стелла", "степанида", "стефания",
					"таисия", "таисья", "тамара", "татьяна", "ульяна",
					"устиния", "устинья", "фаина", "фёкла", "фекла", "феодора",
					"хаврония", "христина", "эвелина", "эдита", "элеонора",
					"элла", "эльвира", "эмилия", "эмма", "юдифь", "юлиана",
					"юлия", "ядвига", "яна", "ярослава" },
			{ "абрам", "аверьян", "авраам", "агафон", "адам", "азар", "акакий",
					"аким", "аксён", "александр", "алексей", "альберт",
					"анатолий", "андрей", "андрон", "антип", "антон",
					"аполлон", "аристарх", "аркадий", "арнольд", "арсений",
					"арсентий", "артем", "артём", "артемий", "артур",
					"аскольд", "афанасий", "богдан", "борис", "борислав",
					"бронислав", "вадим", "валентин", "валерий", "варлам",
					"василий", "венедикт", "вениамин", "веньямин", "венцеслав",
					"виктор", "вилен", "виталий", "владилен", "владимир",
					"владислав", "владлен", "всеволод", "всеслав", "вячеслав",
					"гавриил", "геннадий", "георгий", "герман", "глеб",
					"григорий", "давид", "даниил", "данил", "данила", "демьян",
					"денис", "димитрий", "дмитрий", "добрыня", "евгений",
					"евдоким", "евсей", "егор", "емельян", "еремей", "ермолай",
					"ерофей", "ефим", "захар", "иван", "игнат", "игорь",
					"илларион", "иларион", "илья", "иосиф", "казимир",
					"касьян", "кирилл", "кондрат", "константин", "кузьма",
					"лавр", "лаврентий", "лазарь", "ларион", "лев", "леонард",
					"леонид", "лука", "максим", "марат", "мартын", "матвей",
					"мефодий", "мирон", "михаил", "моисей", "назар", "никита",
					"николай", "олег", "осип", "остап", "павел", "панкрат",
					"пантелей", "парамон", "пётр", "петр", "платон", "потап",
					"прохор", "роберт", "ростислав", "савва", "савелий",
					"семён", "семен", "сергей", "сидор", "спартак", "тарас",
					"терентий", "тимофей", "тимур", "тихон", "ульян", "фёдор",
					"федор", "федот", "феликс", "фирс", "фома", "харитон",
					"харлам", "эдуард", "эммануил", "эраст", "юлиан", "юлий",
					"юрий", "яков", "ян", "ярослав" } };

	public static void main(String[] args) {
		first_name = "Валерия";
		surname = "Иванова";
		System.out.println(gender_by_first_name());
		System.out.println(gender_by_surname());
		
		String screenname = "";
		System.out.println(get_gender(screenname));
	}

	/**
	 * @return {Gender}
	 */
	public static Gender get_gender(String screenname) {
		boolean male = false, female = false;
		Gender gender = Gender.NEUTRAL;
		
		if (screenname == null || screenname.isEmpty())
			return gender;
		
		String s[] = screenname.split(" ");
		first_name = s[0];
		surname = s[1];

		Gender gender_on_fname = gender_by_first_name();
		Gender gender_on_surname = gender_by_surname();

		if (gender_on_fname == Gender.MALE)
			male = true;
		if (gender_on_fname == Gender.FEMALE)
			female = true;
		if (gender_on_surname == Gender.MALE)
			male = true;
		if (gender_on_surname == Gender.FEMALE)
			female = true;

		if (male && !female)
			gender = Gender.MALE;
		if (!male && female)
			gender = Gender.FEMALE;

		return gender;
	};

	/**
	 * Check gender by first name
	 * 
	 * @return {Gender}
	 */
	private static Gender gender_by_first_name() {
		Gender gender = Gender.NEUTRAL;
		if (is_popular_name(first_name, Gender.FEMALE.ordinal()))
			gender = Gender.FEMALE;
		else if (is_popular_name(first_name, Gender.MALE.ordinal()))
			gender = Gender.MALE;
		return gender;
	}

	/**
	 * Search first name in array
	 * 
	 * @return {Boolean}
	 */
	private static boolean is_popular_name(String first_name, int gender) {
		boolean is_popular_name = false;

		for (int i = 0; i < names[gender].length; i++)
			if (first_name.toLowerCase().equals(names[gender][i])) {
				is_popular_name = true;
				break;
			}
		return is_popular_name;
	}

	/**
	 * Check gender by surname
	 * 
	 * @return {Gender}
	 */
	private static Gender gender_by_surname() {
		Gender gender = Gender.NEUTRAL;
		if (is_surname_completes(surname, Gender.FEMALE.ordinal()))
			gender = Gender.FEMALE;
		else if (is_surname_completes(surname, Gender.MALE.ordinal()))
			gender = Gender.MALE;
		return gender;
	}

	/**
	 * Search completion of surname in array
	 * 
	 * @return {boolean}
	 */
	private static boolean is_surname_completes(String surname, int gender) {
		boolean is_surname_completes = false;
		for (int i = 0; i < surname_completions[gender].length; i++) {
			int count = surname_completions[gender][i].length();
			String completion = surname.substring(surname.length() - count)
					.toLowerCase();
			if (completion.equals(surname_completions[gender][i])) {
				is_surname_completes = true;
				break;
			}
		}
		return is_surname_completes;
	}
}
