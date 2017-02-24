SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:	Vetrov
-- Description:	Update extended account props
-- =============================================
ALTER PROCEDURE [dbo].[spAccountUpdExt]
	@user_id BIGINT,
	@twitter_id BIGINT,
	@location NVARCHAR(50),  
	@followers_count INT,
	@friends_count INT,
	@listed_count INT,
	@statuses_count INT,
	@url NVARCHAR(500), 
	@description NVARCHAR(MAX),  
	@created_at datetimeoffset(2),
	@utc_offset INT,
	@time_zone NVARCHAR(50),  
	@lang NVARCHAR(50),  
	@geo_enabled BIT,
	@lasttweet_at datetimeoffset(2),
	@default_profile BIT,
	@default_profile_image BIT,
	@verified BIT
AS BEGIN
	SET NOCOUNT ON

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

	MERGE [dbo].[mAccounts] A
	USING (SELECT [user_id] = @user_id ) I 
		ON A.[user_id] = I.[user_id]
	WHEN MATCHED THEN UPDATE SET
		[twitter_id] = @twitter_id
		,[location] = @location
		,[followers_count] = @followers_count
		,[friends_count] = @friends_count
		,[listed_count] = @listed_count
		,[statuses_count] = @statuses_count
		,[url] = @url
		,[description] = @description
		,[created_at] = @created_at
		,[utc_offset] = @utc_offset
		,[time_zone] = @time_zone
		,[lang_id] = @lang_id
		,[geo_enabled] = @geo_enabled
		,[lasttweet_at] = @lasttweet_at
		,[default_profile] = @default_profile
		,[default_profile_image] = @default_profile_image
		,[verified] = @verified
		,[sinsert] = GETDATE()
	;

END
GO
