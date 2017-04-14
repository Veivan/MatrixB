SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE VIEW dbo.[Count users by groups]
AS
SELECT        TOP (100) PERCENT D.group_id, D.group_name, COUNT(B.user_id) AS cnt, COUNT(A.user_id) AS IsEnabled, COUNT(T.user_id) AS HaveTokens
FROM            dbo.DicGroups AS D INNER JOIN
                         dbo.mBelong2 AS B ON B.group_id = D.group_id LEFT OUTER JOIN
                         dbo.mTokens AS T ON T.user_id = B.user_id LEFT OUTER JOIN
                         dbo.mAccounts AS A ON A.user_id = B.user_id AND A.enabled = 1
GROUP BY D.group_id, D.group_name

