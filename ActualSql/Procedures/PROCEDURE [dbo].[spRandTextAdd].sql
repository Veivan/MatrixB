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
	,@pic_id INT
	,@url NVARCHAR(1000)
	,@twit_id INT
AS BEGIN
	SET NOCOUNT ON;
	IF (@twit_id = 1)
		MERGE [dbo].[mRandText] T
		USING (SELECT [randtext] = @text ) I 
			ON T.[randtext] = I.[randtext]
		WHEN NOT MATCHED BY TARGET THEN INSERT
			([randtext])
		VALUES
			(@text)
		;
	ELSE
		INSERT [dbo].[mRandText] ([randtext], [pic_id], [url], [twit_id])
		VALUES
			(@text, @pic_id, @url, @twit_id)
END
GO
