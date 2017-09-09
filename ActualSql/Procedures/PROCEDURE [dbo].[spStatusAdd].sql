SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- ================================================
-- Author:	Vetrov
-- Description:	Store Status in DB
-- ================================================
ALTER PROCEDURE [dbo].[spStatusAdd]
	@tw_id BIGINT
	,@status NVARCHAR(1000)
	,@creator_id BIGINT
	,@created_at datetimeoffset(2)
	,@favorite_count INT
	,@in_reply_to_screen_name NVARCHAR(50)
	,@in_reply_to_status_id BIGINT
	,@in_reply_to_user_id BIGINT
	,@lang NVARCHAR(50)
	,@retweet_count INT
	,@text NVARCHAR(500)
	,@place_json NVARCHAR(500)
	,@coordinates_json NVARCHAR(500)
	,@favorited BIT
	,@retweeted BIT
	,@isRetweet BIT
	,@retweeted_id BIGINT
AS BEGIN
	SET NOCOUNT ON;

	DECLARE @lang_id INT

	MERGE [dbo].[DicLang] D
	USING (SELECT [lang] = @lang ) I 
		ON D.[lang] = I.[lang]
	WHEN NOT MATCHED BY TARGET THEN INSERT
		([lang])
	VALUES
		(@lang)
	;

	SELECT @lang_id = [lang_id] FROM [dbo].[DicLang] WHERE [lang] = @lang

	MERGE [dbo].[twTwits] T
	USING (SELECT [tw_id] = @tw_id ) I 
		ON T.[tw_id] = I.[tw_id]
	WHEN NOT MATCHED BY TARGET THEN INSERT
		([tw_id],[status], [creator_id], [created_at], [favorite_count], 
		[in_reply_to_screen_name], [in_reply_to_status_id], [in_reply_to_user_id], [lang_id], [retweet_count], 
		[text], [place_json], [coordinates_json], [favorited], [retweeted], [isRetweet], [retweeted_id])
	VALUES
		(@tw_id, @status, @creator_id, @created_at, @favorite_count, 
		@in_reply_to_screen_name, @in_reply_to_status_id, @in_reply_to_user_id, @lang_id, @retweet_count,
		@text, @place_json, @coordinates_json, @favorited, @retweeted, @isRetweet, @retweeted_id)
	WHEN MATCHED THEN UPDATE SET
		[status] = @status
		,[favorite_count] = @favorite_count
		,[retweet_count] = @retweet_count
		,[favorited] = @favorited
		,[retweeted] = @retweeted
	;
END
GO
