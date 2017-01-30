USE MatrixB
SELECT 
	[email]
	,[pass]
	,[screen_name]

	/*[screen_name]
	,[pass]
	,[phone]
	,[email]
	,[mailpass] */
FROM [dbo].[mAccounts] A 
	INNER JOIN [dbo].[mBelong2] B ON B.user_id = A.user_id AND B.group_id = 1
	LEFT JOIN [dbo].[mTokens] T ON T.user_id = A.user_id
	WHERE 
		--A.[enabled] = 1
		--AND
		--A.[url] = '1'
		--AND
		T.id_creds IS NOT NULL

/*
update [dbo].[mAccounts]  set [enabled] = null

UPDATE A
SET A.[enabled] = 1
FROM [dbo].[mAccounts] A 
	INNER JOIN [dbo].[mBelong2] B ON B.user_id = A.user_id AND B.group_id = 1
	LEFT JOIN [dbo].[mTokens] T ON T.user_id = A.user_id
	WHERE 
		T.id_creds IS NOT NULL

*/