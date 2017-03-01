SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Vetrov
-- Description:	Converts BIGINT (unix time) to DATETIME.
-- =============================================
ALTER FUNCTION [dbo].[UnixTime2DateTime](@time BIGINT)
RETURNS DATETIME
AS BEGIN
	DECLARE @result DATETIME
	SELECT @result = dateadd(S, @time, '1970-01-01')	
	RETURN @result
END
GO
