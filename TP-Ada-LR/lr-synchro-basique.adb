with Ada.Text_IO; use Ada.Text_IO;
with Ada.Exceptions;

-- Lecteurs concurrents, approche automate. Pas de politique d'accès.
package body LR.Synchro.Basique is
   
   function Nom_Strategie return String is
   begin
      return "Automate, lecteurs concurrents, sans politique d'accès";
   end Nom_Strategie;
   
   task LectRedTask is
      entry Demander_Lecture;
      entry Demander_Ecriture;
      entry Terminer_Lecture;
      entry Terminer_Ecriture;
   end LectRedTask;

   task body LectRedTask is
     type etatpossible is (libre, lect, red);
     etat : etatpossible := libre;
     nblect : integer := 0;     
   begin
      loop
         case etat is
	   when libre => 
	     select
	     	accept Demander_Lecture; nblect := 1; etat := lect;
	     or

	     	accept Demander_Ecriture; etat := red;
	     end select;
	   when lect =>
	     select
	     	accept Demander_Lecture; nblect := nblect + 1;
		or
		accept Terminer_Lecture;
			if nblect = 1 then nblect := 0; etat := libre; else nblect := nblect - 1; end if;
	     end select;
	   when red =>
	     accept Terminer_Ecriture; etat := libre;
	   
	 end case;
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
