SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON

CREATE VIEW [dbo].[Count users by workgroups]
AS
SELECT        TOP (100) PERCENT group_id, CAST(group_name AS NVARCHAR(50)) AS group_name, cnt, IsEnabled, HaveTokens
FROM            (SELECT        D.group_id, D.group_name, COUNT(B.user_id) AS cnt, COUNT(A.user_id) AS IsEnabled, COUNT(T.user_id) AS HaveTokens
                          FROM            dbo.DicGroups AS D LEFT OUTER JOIN
                                                    dbo.mBelong2 AS B ON B.group_id = D.group_id LEFT OUTER JOIN
                                                    dbo.mTokens AS T ON T.user_id = B.user_id LEFT OUTER JOIN
                                                    dbo.mAccounts AS A ON A.user_id = B.user_id AND A.enabled = 1
						  WHERE D.forwork = 1
                          GROUP BY D.group_id, D.group_name
                          UNION
                          SELECT        1000 AS group_id, 'ITOGO' AS group_name, COUNT(*) AS cnt, SUM(CASE WHEN [enabled] = 1 THEN 1 ELSE 0 END) AS IsEnabled, COUNT(T.user_id) 
                                                   AS HaveTokens
                          FROM            dbo.mAccounts AS A LEFT OUTER JOIN
                                                   dbo.mTokens AS T ON T.user_id = A.user_id) AS A_1
ORDER BY group_id


