USE [MatrixB]
GO
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[DicTwType](
	[id_prj] [int] IDENTITY(1,1) NOT NULL,
	[twit_id] [int] NOT NULL,
	[descript] [nvarchar](1000) NOT NULL
) ON [PRIMARY]

GO

EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'��� �������. ������� � TContent ������.' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'DicTwType', @level2type=N'COLUMN',@level2name=N'twit_id'
GO


BEGIN TRANSACTION
GO
ALTER TABLE dbo.mRandText ADD
	pic_id int NULL,
	url nvarchar(300) NULL,
	twit_id int NOT NULL,
	urlshort [nvarchar](30) NULL

GO
DECLARE @v sql_variant 
SET @v = N'Link to DicTwType'
EXECUTE sp_addextendedproperty N'MS_Description', @v, N'SCHEMA', N'dbo', N'TABLE', N'mRandText', N'COLUMN', N'twit_id'
GO
ALTER TABLE dbo.mRandText SET (LOCK_ESCALATION = TABLE)
GO
COMMIT
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- ================================================
-- Author:	Vetrov
-- Description:	Store random texts in DB
-- ================================================
CREATE PROCEDURE [dbo].[spRandTextAdd]
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
GO

-- =============================================
-- Author:	Vetrov
-- Description:	Add image from binary array
-- @gender = 0 (FEMALE) or = 1 (MALE) or = null (NEUTRAL)
-- @ptype_id is link to DicPicType
-- =============================================
ALTER PROCEDURE [dbo].[spLoadImage]
	@pic VARBINARY(MAX)
	,@gender BIT
	,@ptype_id INT
AS
BEGIN
	SET NOCOUNT ON;	
	INSERT INTO [dbo].[mPicture] (fpicture, gender, ptype_id) VALUES (@pic, @gender, @ptype_id)
	RETURN SCOPE_IDENTITY()
END
GO

-- =============================================
-- Author:	Vetrov
-- Description:	Select random content for twit from DB
-- =============================================
CREATE PROCEDURE [dbo].[spGetRandomContent]
	@twit_id INT
AS
BEGIN
	SET NOCOUNT ON;	
	DECLARE @tmp TABLE(nnid INT IDENTITY(1,1), [rnt_id] BIGINT)
	DECLARE @randomnum INT, @reccnt INT, @rnt_id BIGINT

	INSERT INTO @tmp ([rnt_id])
	SELECT [rnt_id] 
	FROM [dbo].[mRandText] 	WHERE [twit_id] = @twit_id 

	SET @reccnt = @@ROWCOUNT
	SET @randomnum = Ceiling(Rand() * @reccnt)
 
	SELECT @rnt_id = [rnt_id] FROM @tmp 
	WHERE [nnid] = @randomnum

	SELECT [randtext]
      ,[fpicture]
      ,[url]
	  ,[urlshort]
	FROM [dbo].[mRandText] R
		INNER JOIN @tmp T ON T.[rnt_id] = R.[rnt_id]
		LEFT JOIN [dbo].[mPicture] P ON P.[pic_id] = R.[pic_id]
	WHERE T.[nnid] = @randomnum 

END

GO
UPDATE [dbo].[mDBversion]
   SET [version] = '2017.9.13.1'
GO
