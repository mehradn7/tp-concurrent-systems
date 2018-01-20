with Ada.Text_IO; use Ada.Text_IO;
with Ada.Exceptions;

-- Lecteurs concurrents, SERVICE. Pas de politique d'accès.
package body LR.Synchro.Basique is
   
   function Nom_Strategie return String is
   begin
      return "Automate, lecteurs concurrents, SERVICE, sans politique d'accès";
   end Nom_Strategie;
   
   task LectRedTask is
      entry Demander_Lecture;
      entry Demander_Ecriture;
      entry Terminer_Lecture;
      entry Terminer_Ecriture;
   end LectRedTask;

   task body LectRedTask is
     nblect : integer := 0;
     red : boolean := false;
   begin
     loop
       select
       when not red => accept Demander_Lecture; nblect := nblect + 1;
       or
       when (nblect = 0 and (not red)) => accept Demander_Ecriture; red := true;
       or
       when red => accept Terminer_Ecriture;
       or
       when (nblect > 0) => accept Terminer_Lecture; nblect := nblect - 1;
       or 
       terminate;
     end select;
     end loop;

        exception
      when Error: others =>
         Put_Line("**** LectRedTask: exception: " & Ada.Exceptions.Exception_Information(Error));
   end LectRedTask;

   procedure Demander_Lecture is
   begin
      LectRedTask.Demander_Lecture;
   end Demander_Lecture;

   procedure Demander_Ecriture is
   begin
      LectRedTask.Demander_Ecriture;
   end Demander_Ecriture;

   procedure Terminer_Lecture is
   begin
      LectRedTask.Terminer_Lecture;
   end Terminer_Lecture;

   procedure Terminer_Ecriture is
   begin
      LectRedTask.Terminer_Ecriture;
   end Terminer_Ecriture;

end LR.Synchro.Basique;
