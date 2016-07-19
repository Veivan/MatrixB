package jobs;

import settings.Constants;

/** Класс описывает единицу задания - твит, ретвит... .
*/
public class JobAtom {
	public int JobID;

	/** Свойство - тип задания.*/
	public Constants.JobType Type;

	/** Свойство - время выполнения задания. Заполняется при размещении задания в тайминге аккаунта */
	public Long timestamp = 0l;
}
