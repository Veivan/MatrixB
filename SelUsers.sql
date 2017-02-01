USE MatrixB
SELECT 
	[email]
	,[pass]
	,[screen_name]
	,A.[user_id]

	/*[screen_name]
	,[pass]
	,[phone]
	,[email]
	,[mailpass] */
FROM [dbo].[mAccounts] A 
	INNER JOIN [dbo].[mBelong2] B ON B.user_id = A.user_id AND B.group_id = 4
	LEFT JOIN [dbo].[mTokens] T ON T.user_id = A.user_id
	WHERE 
		--A.[enabled] = 1
		--AND
		--A.[url] = '1'
		--AND
		T.id_creds IS NULL

/*
update [dbo].[mAccounts]  set [enabled] = null
update [dbo].[mAccounts]  set [enabled] = 1 where user_id = 12

UPDATE A
SET A.[enabled] = 0
FROM [dbo].[mAccounts] A 
	INNER JOIN [dbo].[mBelong2] B ON B.user_id = A.user_id AND B.group_id = 1
	LEFT JOIN [dbo].[mTokens] T ON T.user_id = A.user_id
	WHERE 
		T.id_creds IS NOT NULL

SELECT COUNT(*) FROM [dbo].[mAccounts] WHERE [enabled] = 1

SELECT E.*
	  ,[date] = dateadd(S, [execdate], '1970-01-01')

  FROM [MatrixB].[dbo].[mExecution] E
  order by ae_id

*/