SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE VIEW dbo.[Count users by app]
AS
SELECT        id_app, aowner, appname, active, cnt
FROM            (SELECT        P.id_app, P.user_id AS aowner, P.appname, P.active, SUM(CASE WHEN A.id_app IS NULL THEN 0 ELSE 1 END) AS cnt
                          FROM            dbo.mApplications AS P LEFT OUTER JOIN
                                                    dbo.mAcc2App AS A ON A.id_app = P.id_app
                          GROUP BY P.id_app, P.user_id, P.appname, P.active
                          UNION
                          SELECT        NULL AS Expr1, NULL AS aowner, 'Itog' AS appname, NULL AS Expr2, COUNT(*) AS cnt
                          FROM            dbo.mAcc2App) AS A_1

SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE VIEW dbo.[Count users by groups]
AS
SELECT        TOP (100) PERCENT group_id, CAST(group_name AS NVARCHAR(50)) AS group_name, cnt, IsEnabled, HaveTokens
FROM            (SELECT        D.group_id, D.group_name, COUNT(B.user_id) AS cnt, COUNT(A.user_id) AS IsEnabled, COUNT(T.user_id) AS HaveTokens
                          FROM            dbo.DicGroups AS D LEFT OUTER JOIN
                                                    dbo.mBelong2 AS B ON B.group_id = D.group_id LEFT OUTER JOIN
                                                    dbo.mTokens AS T ON T.user_id = B.user_id LEFT OUTER JOIN
                                                    dbo.mAccounts AS A ON A.user_id = B.user_id AND A.enabled = 1
                          GROUP BY D.group_id, D.group_name
                          UNION
                          SELECT        1000 AS group_id, 'ITOGO' AS group_name, COUNT(*) AS cnt, SUM(CASE WHEN [enabled] = 1 THEN 1 ELSE 0 END) AS IsEnabled, COUNT(T.user_id) 
                                                   AS HaveTokens
                          FROM            dbo.mAccounts AS A LEFT OUTER JOIN
                                                   dbo.mTokens AS T ON T.user_id = A.user_id) AS A_1
ORDER BY group_id

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

