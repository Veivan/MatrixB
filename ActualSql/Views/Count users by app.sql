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

