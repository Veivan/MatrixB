SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE VIEW dbo.[Temporary locked accounts]
AS
SELECT        A.user_id, A.enabled, T.id_creds, B.group_id, X.fr_id
FROM            dbo.mAccounts AS A INNER JOIN
                         dbo.mBelong2 AS B ON A.user_id = B.user_id INNER JOIN
                         dbo.DicGroups AS D ON B.group_id = D.group_id LEFT OUTER JOIN
                         dbo.mTokens AS T ON T.user_id = A.user_id LEFT OUTER JOIN
                             (SELECT DISTINCT user_id, fr_id
                               FROM            dbo.mExecution AS E
                               WHERE        (fr_id = 6)) AS X ON X.user_id = A.user_id
WHERE        (A.enabled = 0) AND (T.id_creds IS NOT NULL)

