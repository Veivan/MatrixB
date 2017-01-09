USE MatrixB
SELECT 
	[name]
	,[pass]
	,[phone]
	,[email]
	,[mailpass]
FROM [dbo].[mAccounts] A 
	INNER JOIN [dbo].[mBelong2] B ON B.user_id = A.user_id AND B.group_id = 2
	LEFT JOIN [dbo].[mTokens] T ON T.user_id = A.user_id
	WHERE T.id_creds IS NULL

