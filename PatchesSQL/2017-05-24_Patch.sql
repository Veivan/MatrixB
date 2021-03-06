USE [MatrixB]
GO
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[mGroupRegim](
	[grg_id] [int] IDENTITY(1,1) NOT NULL,
	[groupid] [int] NOT NULL,
	[WakeHour] [int] NOT NULL,
	[BedHour] [int] NOT NULL
) ON [PRIMARY]

GO

		INSERT INTO mGroupRegim(groupid,WakeHour,BedHour) VALUES(5, 3, 7)
		INSERT INTO mGroupRegim(groupid,WakeHour,BedHour) VALUES(9, 7, 11)
		INSERT INTO mGroupRegim(groupid,WakeHour,BedHour) VALUES(10, 11, 15)
		INSERT INTO mGroupRegim(groupid,WakeHour,BedHour) VALUES(11, 15, 19)
		INSERT INTO mGroupRegim(groupid,WakeHour,BedHour) VALUES(12, 19, 23)
GO

UPDATE [dbo].[mDBversion]
   SET [version] = '2017.5.24.1'
GO

