SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- ================================================
-- Author:	Vetrov
-- Description:	Store random texts in DB
-- ================================================
ALTER PROCEDURE [dbo].[spRandTextAdd]
	@text NVARCHAR(MAX)
AS BEGIN
	SET NOCOUNT ON;

	MERGE [dbo].[mRandText] T
	USING (SELECT [randtext] = @text ) I 
		ON T.[randtext] = I.[randtext]
	WHEN NOT MATCHED BY TARGET THEN INSERT
		([randtext])
	VALUES
		(@text)
	;
END
GO
