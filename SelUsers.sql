
SELECT 
	[email]
	,[pass]
	,[name]
	[user_id]
FROM [MatrixB].[dbo].[mAccounts] A 
	LEFT JOIN [MatrixB].[dbo].[mTokens] T ON T.user_id = A.user_id
	WHERE T.id_creds IS NULL